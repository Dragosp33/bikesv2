# Use an official Nginx image as the base image
ARG NODE_VERSION=20.4.0
FROM node:${NODE_VERSION}-slim as base

LABEL fly_launch_runtime="Node.js"

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json to leverage Docker cache for dependencies


# Copy the built Angular app into the Nginx web server directory
COPY ./dist/angular_template ./dist/angular_template

COPY ./server.js ./

#COPY ./package.json ./
#COPY ./package-lock.json ./
#RUN npm install
RUN npm install express

# Expose the default Nginx port
EXPOSE 80



# Start Nginx when the container starts
CMD ["node", "server.js"]