# -*- coding: utf-8 -*-
"""备忘录业务逻辑"""
import json
from datetime import datetime

from app.database import exec_sql, fetchone, get_db


def list_notes(user_id: int) -> list:
    with get_db() as conn:
        cur = exec_sql(
            conn,
            "SELECT * FROM notes WHERE user_id = %s ORDER BY updated_at DESC",
            (user_id,),
        )
        rows = []
        for r in cur.fetchall():
            d = dict(r)
            if d.get("tags"):
                try:
                    d["tags"] = json.loads(d["tags"]) if isinstance(d["tags"], str) else d["tags"]
                except (json.JSONDecodeError, TypeError):
                    d["tags"] = []
            else:
                d["tags"] = []
            rows.append(d)
        return rows


def create_note(user_id: int, data: dict) -> dict:
    title = str(data.get("title", "")).strip()
    if not title:
        raise ValueError("标题不能为空")
    content = str(data.get("content", "")).strip()
    tags = data.get("tags") or []

    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    with get_db() as conn:
        cur = exec_sql(
            conn,
            """INSERT INTO notes (user_id, title, content, tags, created_at, updated_at)
               VALUES (%s, %s, %s, %s, %s, %s) RETURNING *""",
            (user_id, title, content, json.dumps(tags, ensure_ascii=False), now, now),
        )
        row = fetchone(cur)
        if row and row.get("tags"):
            try:
                row["tags"] = json.loads(row["tags"]) if isinstance(row["tags"], str) else row["tags"]
            except (json.JSONDecodeError, TypeError):
                row["tags"] = []
    return row


def update_note(user_id: int, note_id: int, data: dict) -> dict:
    title = str(data.get("title", "")).strip()
    if not title:
        raise ValueError("标题不能为空")
    content = str(data.get("content", "")).strip()
    tags = data.get("tags") or []

    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    with get_db() as conn:
        cur = exec_sql(
            conn,
            """UPDATE notes SET title=%s, content=%s, tags=%s, updated_at=%s
               WHERE id=%s AND user_id=%s RETURNING *""",
            (title, content, json.dumps(tags, ensure_ascii=False), now, note_id, user_id),
        )
        row = fetchone(cur)
        if row and row.get("tags"):
            try:
                row["tags"] = json.loads(row["tags"]) if isinstance(row["tags"], str) else row["tags"]
            except (json.JSONDecodeError, TypeError):
                row["tags"] = []
    return row


def delete_note(user_id: int, note_id: int) -> int:
    with get_db() as conn:
        cur = exec_sql(
            conn,
            "DELETE FROM notes WHERE id=%s AND user_id=%s",
            (note_id, user_id),
        )
        return cur.rowcount
