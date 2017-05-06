#! /usr/bin/env python3
# -*- coding: utf-8 -*-


from flask import Flask
from flask import abort
from flask import jsonify
from flask import request


app = Flask(__name__)
users = list()


@app.route('/')
def index():
    """ важная информация """
    return jsonify(message='awesome api version 1.337rc powered by morkovk')


@app.route('/users')
def all():
    """ Список всех пользователей """
    return jsonify(users=users)


@app.route('/users/<int:id>')
def get(id):
    """ Получить пользователя по ID """
    user = next((x for x in users if x['id'] == id), None) or abort(404)
    return jsonify(user)


@app.route('/users', methods=['POST'])
def add():
    """ Добавить нового пользователя """
    if not request.json:
        abort(400)

    if request.json.keys() != {'name', 'phone'}:
        abort(422)

    request.json['id'] = users[-1]['id'] + 1 if len(users) else 1
    users.append(request.json)

    return jsonify(message='ok')


@app.route('/users/<int:id>', methods=['PUT'])
def update(id):
    """ Обновить пользователя по ID """
    if not request.json:
        abort(400)

    if request.json.keys() != {'name', 'phone'}:
        abort(422)
    
    index = next((i for i, x in enumerate(users) if x['id'] == id), None) or abort(404)
    users[index].update(request.json)
    
    return jsonify(message='ok')


@app.route('/users/<int:id>', methods=['DELETE'])
def delete(id):
    """ Удалить пользователя по ID """
    user = next((x for x in users if x['id'] == id), None) or abort(404)
    users.remove(user)

    return jsonify(message='ok')


@app.errorhandler(400)
def bad_request(e):
    """ кривой запрос """
    return jsonify(message='Bad request'), 400


@app.errorhandler(422)
def unprocessable_entity(e):
    """ запрос инвалида """
    return jsonify(message='Unprocessable Entity'), 422


@app.errorhandler(404)
def not_found(e):
    """ потерянный юноша """
    return jsonify(message='User not found'), 404


if __name__ == '__main__':
    """ беги лес беги """
    app.run(host='0.0.0.0', port=1337, debug=True)

