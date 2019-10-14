# Welcome to the Rafiki Wiki!

[![Build Status](https://travis-ci.org/markalence/rafiki.svg?branch=master)](https://travis-ci.org/markalence/rafiki)
[![Coverage Status](https://coveralls.io/repos/github/markalence/rafiki/badge.svg?branch=master)](https://coveralls.io/github/markalence/rafiki?branch=master)

## Project members:
* Noah Savadier
* Daniel Yazbek
* Daniel Hewlett
* Mark Alence 

## Project brief:

A common objective across universities and their faculties is improving engagement between students and their course material. 

The aim of Rafiki is to improve active engagement between students and their course material by providing students with the ability to get the most out of the resources and insights provided by lectures, tutorials and practical sessions through Q/A posting and student discussion forums. The application further closes the gap between students and the assistance they need by allowing for direct communication between students and course facilitators such as tutors and lecturers.  

The Rafiki team believes in everyone achieving the best they can.

## Continuous Integration:
Throughout the development of the Rafiki system, GitHub will be/is the main code repository for the project. Through the implementation of TravisCI and Coveralls, every push and change is built, run, analysed and tested to ensure only clean, functional code with sufficient coverage lies within the repository.

## Structure of application:
The Rafiki system is to be implemented using a distributed Android mobile application created with the Android Studio suite.

## Project Implementation:
### Server/Database
The Rafiki application will heavily utilize the Firebase support package developed by Google for data analytics, database storage and crash support specifically catered for mobile applications. Rafiki will further make use of Google Cloud which allows for scheduling services essential to the application.

## High level documentation:
### Architectural Style: 3-tier
![Architecture (1)](https://user-images.githubusercontent.com/47977629/63334066-dd4f6c00-c33a-11e9-97e2-3432cfb49431.png)


The 3-tier architectural style is well suited for the Rafiki system. Due to the nature of the application, the necessities required include some form of middle-management services (to handle user registration, event handling, database communication, etc) and a real-time database for storing application and user data. A 3-tier style therefore is suitable. 

Furthermore, the 3-tier style overall tends itself to faster performance, a less complex development environment and greater scalability.


## 4+1 Architectural view model
### Development view: Component diagram
The Rafiki Android mobile application is simplistic in its make-up. 

![Component diagram](https://user-images.githubusercontent.com/47977629/65031607-d0c23180-d941-11e9-8d7e-35df9ccd6eea.png)

The mobile application contains most of the logic and computational responsibilities. The application component of the system contains several realizations that contain the core functionality of the application. The application component communicates with the Firebase console component and Google Cloud component through the respective Google Firebase and Google Cloud APIs.  

The Firebase console and Google Cloud components are independent in nature and handle all provided services/analytics. The Firebase console component further handles data requests by communicating through a NoSQL interface with the Cloud Firestore database component which is responsible for storing all Rafiki logic data and user data. 

### Physical view: Deployment diagram

The deployment diagram observed is comprised of two major devices, the mobile Android device and the Google servers which communicate over a HTTP/SSL connection.
![Deployment Diagram](https://user-images.githubusercontent.com/47977629/65031604-d0299b00-d941-11e9-92e3-e92791698b20.png)

The mobile Android device is host to and responsible for the Rafiki mobile APK. Within the Rafiki application there are several components that provide the core functionality of the application and are responsible for communication with Google Firebase and Google Cloud.

The Google cloud servers contains several elements. The Firebase console component is essential for managing the services/analytics that Google Firebase provides but it is also crucial for managing data requests from the Rafiki mobile application. The console communicates with the Cloud Firestore database which has a container for storing all relevant information created by the Rafiki application as collections. Lastly the Google cloud servers contain the Google Cloud console which facilitates the utilisation of Google Cloud Services. This console does not explicitly make use of a database component.

### Scenario: Use case diagram

The scenario view of this project currently includes two agents. The Rafiki system and the Rafiki user. 
![WitsRewards Use Case Diagram](https://user-images.githubusercontent.com/47977629/65031603-d0299b00-d941-11e9-9547-a1a3d82fc3b2.png)

Currently, the user is firstly provided with the functionality to register and login into the Rafiki application. The user is secondly able to post questions about currently posted course events. Furthermore, the user can scan QR codes which will in future allow them to check in to course events using provided QR codes and potentially earn them course points.


### Process view: Activity diagram
![Activity Diagram](https://user-images.githubusercontent.com/47977629/65032913-65c62a00-d944-11e9-8a66-dec7ea345ad4.png)


The above activity diagram is a graphical representation of process flow, from activity to activity, of the current functionality of the Rafiki Android mobile application. 

Upon opening the application, the user is intuitively presented with the option of either registering or logging into Rafiki. If registration is selected the user is prompted to fill in their personal information. If the registration is successful the user is taken to the home screen. Similarly if the user chooses login and logs in successfully, they are taken to the home screen.  

Once present at the home screen, the user will be presented with previously posted course related questions and their answers. There are several options present for the user to select.

If the user chooses question services then they will provided with the related text fields to do so. Similarly if QR code services is chosen, the camera application will open and the user will be able to scan QR codes. Lastly, the user can view upcoming events in the event services activity.

### Logical view: Class diagram

The logical view of the system is concerned with the overall functionality that is provided to the user from a class perspective.

![Class diagram (1)](https://user-images.githubusercontent.com/47977629/63343192-a9ca0d00-c34d-11e9-95c7-1dc820deae68.png)


As observed in the diagram above, the student class is at the core of functionality.The statistic class exists solely due to the existence of the student class. However, the event, question, location and QR code elements are independent of the student class and exist one their own. The event, questions, location and QR code classes all have some form of relation to the statistics class due to the fact that a student attending course events and posting and answering questions will reflect on their profile.


