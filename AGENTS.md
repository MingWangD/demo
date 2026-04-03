# AGENTS.md

## Project summary
This repository contains:
- Backend: Spring Boot project in `springboot/`
- Frontend: Vue project in `vue/`

## Ground rules
- Do not guess project structure.
- Always inspect the repository before running commands.
- Backend commands must be run from `springboot/`.
- Frontend commands must be run from `vue/`.
- If a required tool is missing, report it clearly instead of guessing fixes.
- Prefer small, verifiable steps.
- When a command fails, report:
    1. working directory
    2. exact command
    3. full error output
    4. likely cause

## Required preflight checks
Before any backend task, run:
1. `pwd`
2. `ls`
3. `cd springboot && ls`
4. `cd springboot && java -version`
5. `cd springboot && mvn -version`

Before any frontend task, run:
1. `pwd`
2. `ls`
3. `cd vue && ls`
4. `cd vue && node -v`
5. `cd vue && npm -v`

## Backend workflow
Use these commands only from `springboot/`:
- Build: `mvn -DskipTests package`
- Run: `mvn spring-boot:run`
- Test: `mvn test`

If Maven fails to start:
- First determine whether `mvn` is missing
- Then determine whether Java is missing or incompatible
- Then determine whether `pom.xml` is missing in the current directory
- Then determine whether dependency download failed

## Frontend workflow
Use these commands only from `vue/`:
- Install: `npm install`
- Dev: `npm run dev`
- Build: `npm run build`

## Validation priority
For quick validation:
1. Backend: `cd springboot && mvn -DskipTests package`
2. Frontend: `cd vue && npm install && npm run build`

## Debugging expectations
If backend startup fails:
- Check whether the current directory contains `pom.xml`
- Check Java version
- Check Maven version
- Show the first failing stack trace line
- Do not claim "project cannot run" unless the failure is reproduced with command output

## Output format
When reporting results, use this structure:
- Step performed
- Command run
- Result
- If failed: exact error and next corrective action