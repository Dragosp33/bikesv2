# Bikes Reservation App

Overview:

Available at : https://bikesv2.fly.dev

Log in test account:

- dragosp0201@gmail.com
- parola2

- Artifacts:
- - Backend sourcecode
- - Frontend Source code & package

# Update 3 - v1.3

30.01.2024

# Software Architecture report

## Purpose and motivation:

- Our vision is to create a seamless and efficient solution for urban commuters, offering a better way to navigate through city traffic. By providing a bike reservation app, we aim to revolutionize the daily commute experience, making it convenient, eco-friendly, and enjoyable.
- We also aim to enhance bikes owners rental capabilities by crating a renting platform for business owners

## Fulfilled capabilities:

- At the current time, the main target is satisfied, as we provide a platform for bikes rental with a seamless user experience and payment method.
- For future releases, user experience should be enhanced to be more appealing and partners portal should open

## How to:

### Run locally:

- Prerequisites:

* node package manager - node (https://nodejs.org/en/download)
* Java Development Kit (JDK) 17
* Maven 3

! important - don't forget:

- Create .env file for frontend - this is where your api keys will be
- Modify `application.properties` in spring boot - for the same reason

1. Clone the project with `git clone https://github.com/inginerie-software-2023-2024/proiect-inginerie-software-ando-foro.git`
2. Go to frontend (`cd frontend`) and run `npm install` - this will install dependecies;
3. In frontend run `npm run start` -> your local development should start at `localhost:4200`
4. Go to backend (`cd backend`) and run `mvn clean install` and `mvn spring-boot:run` -> your local server should start at `localhost:8080`

### Deploy to fly.io:

This application production version is released via [`fly.io`]("https://fly.io")

1. Repeat steps 1. and 2. mentioned above.
2. Install `flyctl` via `pwsh -Command "iwr https://fly.io/install.ps1 -useb | iex"` (for windows) or [follow instructions](https://fly.io/docs/hands-on/install-flyctl/) for other systems
3. go to `frontend` and run `flyctl launch` - this will initialize a new application.
4. Create express server to serve the frontend dist ( copy the `/frontend/server.js` file )
5. Run `ng build` or `npm run build` to build files for production
6. Create docker container to publish / pull locally -- This is an extra step, just in case your working repository is not public --
   6.1. Follow the `.github/workflows` folder for instructions on how to containarize this project
   6.2 `autodeploy` - backend ; `autodeploy-frontend` - frontend
7. Edit `fly.toml` image to push from your docker container and use the express server ( see `/frontend/fly.toml`)
8. For a better performance the springboot application was packed using `paketo` packet manager
9. Repeat step 3 but for `/backend`
10. Create docker container for your backend ( same as frontend, step 6 )
11. Repeat step 7 for backend `fly.toml`;
12. Remember to edit your frontend `environment` file to include the new backend deployed app as `apiUrl`

## Contribution guide:

### Set up your local environment using instructions above then:

- Request configurations from a team member, or a placeholder of our models and data
  Or
- Set up your own: We use a mysql database, so you can host your own on amazon rds, the models will be configured automatically once the application is run for the first time; You should also create a [`stripe`](https://stripe.com/docs/api) developer account for testing and developing purposes.

- Name and store files accordingly. For example, in frontend, a `service` that is not directly dependant on the whole application ( is modular ) should be stored inside the `services` folder.
  !! Recommended: When modifying the frontend, create new `components`/`services` by using `ng g component {filename}` / `ng g service {filename}`.
  Functions are better to be named via `camelCase`, and ideally used with a brief explanation using `/** explanation **/`

### Creating merge and pull requests:

- When contributing, provide a meaningful message, ideally with a issue number so it's easier to track. You can also create a branch with a brief description as name.

### Patterns used:

- MVC ( Model - view - Controller ) pattern with interfaces for better understanding the data types;
- Microservices - each action was decomposed to smaller actions that communicate through the api layer or third parties;
- Observer pattern
- Dependecy injection
- Beans configurations for backend
- Repository pattern to access data

## Application entry points:

- Data sources: Mysql database hosted on amazon cloud rds instance + Stripe portal + firebase auth
- Data inputs: At every reservation a `reservation` object is stored and updated if there was a cancel or refund
- Configuration files:
  `environment.ts` for frontend where configurations of firebase auth and public `stripe key` are
  `application.properties` for backend where database and `firebase admin` configs are stored. This can be removed when using fly by setting enviornments via `flyctl` at deployment time

## QA Process:

Tests are made in frontend using `karma` and `jasmine` to test initialization of components and functionality of services, for example the authentication service used inside the `admin` page, where if the mocked token does not contain the custom claim of `admin` , the user will be redirected to the `/profile` page.

## External dependencies:

### APIs used:

[Firebase](https://firebase.google.com/docs/auth)
[Stripe]("https://stripe.com/docs/api")
[AWS]("https://aws.amazon.com/lambda")
[SENDGRID]("https://sendgrid.com/en-us")

### Libraries:

Bootstrap
OWL-Carousel
Karma
Jasmine
Admin SDK
