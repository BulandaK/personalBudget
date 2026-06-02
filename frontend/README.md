# Personal Budget — Simple API tester

This is a minimal single-file tester for the OpenAPI in `api-docs.json`.

Features:
- Loads `api-docs.json` and lists endpoints grouped by tags
- Builds forms for path/query/body parameters
- Sends requests to the first server defined in the spec (default `http://localhost:8080`)
- Pretty-prints JSON responses

How to run locally:

1. Start a simple static server in this folder (for example):

```bash
python3 -m http.server 5500
```

2. Open your browser at: `http://localhost:5500` and use the UI.

Notes:
- The tester uses Tailwind CDN and htmx for small interactions. The actual request is performed using `fetch()` so JSON responses are displayed nicely.
- If the API server runs on `http://localhost:8080` (as in `api-docs.json`), the UI will call that server. Adjust the `servers` entry in `api-docs.json` if necessary.

Enjoy — jeśli chcesz, mogę dodać autentykację, zapis historii żądań lub generowanie przykładowych payloadów bardziej szczegółowo.
