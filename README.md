# DaarProjectA
## Prerequisites

Before setting up the project, ensure you have the following installed on your system:

### JDK 17

Download and install JDK 17:

```sh
wget https://download.oracle.com/java/17/archive/jdk-17.0.9_linux-x64_bin.deb
sudo dpkg -i jdk-17.0.9_linux-x64_bin.deb
```

### Maven

Install Maven using the following command:

```sh
sudo apt-get install maven
```

### Node.js

Install Node.js version 18:

```sh
sudo apt-get update && sudo apt-get install -y ca-certificates curl gnupg
curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | sudo gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg
NODE_MAJOR=18 
echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_$NODE_MAJOR.x nodistro main" | sudo tee /etc/apt/sources.list.d/nodesource.list
sudo apt-get update && sudo apt-get install nodejs -y
```

### NPM

Install NPM using the following command:

```sh
sudo apt install npm
```

### Angular CLI

Install Angular CLI globally:

```sh
npm install -g @angular/cli
```

### OpenSSL Legacy Provider

Set the OpenSSL legacy provider environment variable:

```sh
export NODE_OPTIONS=--openssl-legacy-provider
```
## Running the Application
### Running the API

To launch the API, navigate to the `api` directory and run the following command:

```sh
cd api/
mvn spring-boot:run
```

### Running the Frontend

To launch the frontend, navigate to the `frontend` directory, install the dependencies, and start the server:

```sh
cd frontend/
npm install
ng serve --open
```

After running these commands, the frontend should be accessible in your web browser.

