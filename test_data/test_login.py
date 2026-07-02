import urllib.request
import json

BASE_URL = 'http://127.0.0.1:8080'

def post(path, data, token=None):
    req = urllib.request.Request(
        BASE_URL + path,
        data=json.dumps(data).encode(),
        headers={'Content-Type': 'application/json'},
        method='POST'
    )
    if token:
        req.headers['Authorization'] = 'Bearer ' + token
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        body = e.read().decode()
        try:
            return json.loads(body)
        except:
            return {'error': f'{e.code}: {body}'}

result = post('/api/sys/login', {'username': 'admin', 'password': 'Admin@123'})
print('登录结果:', result)
