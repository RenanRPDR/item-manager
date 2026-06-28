# Technical Exercise & Interview — Backend Java Developer

## About the exercise
We’d like you to build a small, runnable Spring Boot service with a lightweight UI.

The aim isn’t to produce a polished product. The exercise is intended to give us something practical
to discuss during the interview: how you design APIs, structure code, handle data at a modest
scale, and use AI tooling as part of your development process.

We are more interested in clear, pragmatic decisions than a large number of features.

## What to build
Build a small service and front end centred around a listing page that can comfortably work with
1,000+ items.

### Backend
- A list/search endpoint
- Sensible handling of 1,000+ records
- Endpoints to add and remove items

Persistence can be lightweight. An in-memory or embedded database, such as H2, is fine. The
important thing is that the service is easy to run and reason about.


### Front end
- A listing page
- The ability to add and remove items
- A responsive experience with 1,000+ records in the data set

### Running the application
Please include a clear README so we can get the application running quickly.

- mvn spring-boot:run
- a simple front-end dev server command
- or docker compose up

Please include seed/sample data so the listing page has a realistic volume of data when the application starts.

### Deliverables
- A link to a Git repository with full commit history
- Any AI assistant transcripts you used while building the solution
- A README covering setup instructions, run instructions, and any important decisions or trade-offs

We are interested in how the solution evolved, so prefer incremental, meaningful commits over a single final drop.

AI use is actively encouraged. We just want visibility into how you used it: what you asked, what it produced, and how you applied or changed the output.

### Scope and time
- Keep the exercise small and focused.
- We would expect this to take roughly 3–4 hours.
- Please do not over-invest beyond that.
- If you run out of time, make a note of what you would do next.

### What we are looking for
- Clear, sensible API design
- Server-side handling of search, filtering and pagination, rather than loading everything into the
browser
- Readable, well-structured code
- A codebase that is easy to navigate and extend
- Good Git hygiene, with a history that tells a coherent story
- Transparent and effective use of AI
- A simple but functional UI
- Pragmatic choices appropriate to a small service


### The interview
- The interview will be a 45-minute live pairing session with engineers at thortful.
- During the session, we will ask you to add a feature to your solution.
- AI tools are allowed and encouraged, so please bring your normal setup. We are interested in how you navigate and extend your own codebase, reason through decisions, and work with AI in real time.
- Please have your environment ready before the session so you can run and edit the code.


### A few notes
- Technology choices are yours
- apart from the backend, and
- which should use Spring Boot