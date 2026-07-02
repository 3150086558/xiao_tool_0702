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
        return {'error': f'{e.code}: {e.read().decode()}'}

def get(path, token=None):
    req = urllib.request.Request(BASE_URL + path, method='GET')
    if token:
        req.headers['Authorization'] = 'Bearer ' + token
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        return {'error': f'{e.code}: {e.read().decode()}'}

def put(path, data, token=None):
    req = urllib.request.Request(
        BASE_URL + path,
        data=json.dumps(data).encode(),
        headers={'Content-Type': 'application/json'},
        method='PUT'
    )
    if token:
        req.headers['Authorization'] = 'Bearer ' + token
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        return {'error': f'{e.code}: {e.read().decode()}'}

def delete(path, token=None):
    req = urllib.request.Request(BASE_URL + path, method='DELETE')
    if token:
        req.headers['Authorization'] = 'Bearer ' + token
    try:
        with urllib.request.urlopen(req) as resp:
            return json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        return {'error': f'{e.code}: {e.read().decode()}'}

print('=== 1. 登录 ===')
result = post('/api/sys/login', {'username': 'admin', 'password': 'Admin@123'})
if 'error' in result:
    print('   登录失败:', result['error'])
    exit()
token = result['data']['token']
print('   登录成功')

print()
print('=== 2. 记账分页列表 ===')
r = get('/api/app/accounting/page?page=1&size=10', token)
if 'error' in r:
    print('   失败:', r['error'])
else:
    print('   成功, 记录数:', len(r.get('data', {}).get('records', [])))

print()
print('=== 3. 新增记账 ===')
r = post('/api/app/accounting', {
    'record_date': '2025-07-02',
    'type': 'income',
    'category': '测试工资',
    'sub_category': '工资',
    'amount': 10000,
    'account': '银行卡',
    'note': '测试新增'
}, token)
if 'error' in r:
    print('   失败:', r['error'])
else:
    print('   成功, code:', r.get('code'), 'id:', r.get('data', {}).get('id'))
    record_id = r.get('data', {}).get('id')

print()
print('=== 4. 编辑记账 ===')
if 'record_id' in dir():
    r = put(f'/api/app/accounting/{record_id}', {
        'record_date': '2025-07-03',
        'type': 'expense',
        'category': '测试工资-修改',
        'sub_category': '餐饮',
        'amount': 500,
        'account': '微信',
        'note': '测试编辑'
    }, token)
    if 'error' in r:
        print('   失败:', r['error'])
    else:
        print('   成功, code:', r.get('code'))

print()
print('=== 5. 删除记账 ===')
if 'record_id' in dir():
    r = delete(f'/api/app/accounting/{record_id}', token)
    if 'error' in r:
        print('   失败:', r['error'])
    else:
        print('   成功, code:', r.get('code'))

print()
print('=== 6. 待办新增 ===')
r = post('/api/app/todo', {
    'title': '测试待办',
    'priority': 'high',
    'due_date': '2025-07-05'
}, token)
if 'error' in r:
    print('   失败:', r['error'])
else:
    print('   成功, code:', r.get('code'))

print()
print('=== 7. 备忘录新增 ===')
r = post('/api/app/note', {
    'title': '测试备忘录',
    'content': '测试内容'
}, token)
if 'error' in r:
    print('   失败:', r['error'])
else:
    print('   成功, code:', r.get('code'))

print()
print('=== 8. 统计汇总 ===')
r = get('/api/app/stats/summary', token)
if 'error' in r:
    print('   失败:', r['error'])
else:
    print('   成功, code:', r.get('code'))

print()
print('=== 所有测试完成 ===')
