import r2pipe
import json
import time
import sys

def ida_analyze(so_path, callback):
    r2 = r2pipe.open(so_path)
    r2.cmd("aaa")
    
    progress = 0
    statuses = ["Loading binary...", "Analyzing symbols...", "Finding functions...", "Crossrefs...", "Strings...", "Complete!"]
    
    for i, status in enumerate(statuses):
        time.sleep(0.5)
        progress = int((i + 1) * 100 / len(statuses))
        callback(json.dumps({
            "progress": progress,
            "status": status,
            "functions": len(r2.cmdj("aflj") or [])
        }))
    
    funcs = r2.cmdj("aflj") or []
    strings = r2.cmdj("/j") or []
    
    html = f"""
<html><head><style>
body {{background:black;color:lime;font-family:'Courier New',monospace;padding:20px}}
.func {{color:#00ff00;margin:5px 0}}
.str {{color:#ffaa00}}
</style></head>
<body>
<h2>IDA Pro Style Analysis</h2>
<p>Functions: <b>{len(funcs)}</b> | Strings: <b>{len(strings)}</b></p>
<div class='funcs'>
{''.join([f"<div class='func'>{f['name']} +{hex(f['size'])}</div>" for f in funcs[:50]])}
</div>
</body></html>"""
    
    callback(json.dumps({"progress": 100, "html": html, "done": True}))
    r2.quit()

if __name__ == "__main__":
    def dummy_cb(data): print(data)
    ida_analyze(sys.argv[1], dummy_cb)