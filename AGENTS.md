## Repo structure
- Backend is in `springboot/`
- Frontend is in `vue/`

## Rules
- Never run Maven from repo root
- Backend commands must run in `springboot/`
- Frontend commands must run in `vue/`
- Always check environment before build
- If backend dependency download fails, classify it as environment/network issue before blaming code
- Do not continue to API or E2E tests unless backend can build or start

## Validation order
1. Frontend build
2. Backend dependency check
3. Backend build
4. API tests
5. E2E tests

## Failure reporting
For every failed step, report:
- current directory
- exact command
- full error output
- whether it is environment, code, or data issue
