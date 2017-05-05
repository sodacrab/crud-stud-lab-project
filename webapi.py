from flask import Flask, jsonify, request

app = Flask(__name__)

tasks = [
    {
        'id': 1,
        'name': 'IwanP',
        'phone': '89142892929'
    },
    {
        'id': 2,
        'name': 'VictorSh',
        'phone': '+79521919111'
    }
]

@app.route('/')
def index():
    return jsonify(tasks=tasks)

@app.route('/todo/api/v1.0/tasks', methods=['GET'])
def get_tasks():
    return jsonify(tasks=tasks)

@app.route('/todo/api/v1.0/tasks', methods=['POST'])
def create_task():
    #if not request.json or not 'title' in request.json:
    #    abort(400)
    task = {
        'id': tasks[-1]['id'] + 1,
        'name': request.json['name'],
        'phone': request.json.get('phone', "")
    }
    tasks.append(task)
    return jsonify({'task': task}), 201

@app.route('/todo/api/v1.0/tasks/<int:task_id>', methods=['PUT'])
def update_task(task_id):
    task = [task for task in tasks if task['id'] == task_id]
    if len(task) == 0:
        abort(404)
    if not request.json:
        abort(400)
    if 'name' in request.json and type(request.json['name']) != unicode:
        abort(400)
    if 'phone' in request.json and type(request.json['phone']) is not unicode:
        abort(400)
    task[0]['name'] = request.json.get('name', task[0]['name'])
    task[0]['phone'] = request.json.get('phone', task[0]['phone'])
    return jsonify({'task': task[0]})

@app.route('/todo/api/v1.0/tasks/<int:task_id>', methods=['DELETE'])
def delete_task(task_id):
    task = [task for task in tasks if task['id'] == task_id]
    if len(task) == 0:
        abort(404)
    tasks.remove(task[0])
    return jsonify({'result': True})

if __name__ == '__main__':
   	app.run(host='0.0.0.0', port=1337)