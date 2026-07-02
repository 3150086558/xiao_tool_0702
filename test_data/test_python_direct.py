import urllib.request
import json

BASE_URL = 'http://127.0.0.1:8000'

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

def get(path, token=None):
    req = urllib.request.Request(BASE_URL + path, method='GET')
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

print('=== 直接测试 Python 服务 ===')
token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsImlhdCI6MTc4Mjk4MzYwNSwiZXhwIjoxNzgzNTg4NDA1fQ.6nZIHsXt65-0Ee15aI3k_hAwjF_dFVDkii4aqRddxSQ'

print()
print('1. 记账分页:')
r = get('/api/app/accounting/page?page=1&size=10', token)
print('   结果:', r)

print()
print('2. 新增记账:')
r = post('/api/app/accounting', {
    'record_date': '2025-07-02',
    'type': 'income',
    'category': '测试工资',
    'sub_category': '工资',
    'amount': 10000,
    'account': '银行卡',
    'note': '测试新增'
}, token)
print('   结果:', r)
