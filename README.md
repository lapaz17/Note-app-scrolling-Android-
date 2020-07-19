# Noto

#### Android notes application built using kotlin.

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=com.noto)

## Motivation
The app is a part of my [Portfolio](https://alialbaali.com) projects. It showcases my skills regarding developing Android Apps.

## Screenshots

<img src="https://lh3.googleusercontent.com/gZEnPNJ8S5OCvD13L_6p53LAgsJVS1Lx_Zypzf9aQ4JTuNoZjSyZbHFaZlSddybJq7w=w720-h310" alt="Screenshot">
<img src="https://lh3.googleusercontent.com/P6SrNqnnjOSgSQMWIMvbLB1-2b7sE7LLELDgJIPKiXNd45dQy2OGbSvrZ35pVabxUgY=w720-h310" alt="Screenshot">
<img src="https://lh3.googleusercontent.com/lVdn68iYeecY47f82RZWzTwSxXDoBgnX6ku0dnjDsrm6_Z6-fmoDWaev_h1hUL4OQBtR=w720-h310" alt="Screenshot">
<img src="https://lh3.googleusercontent.com/lo7bvJJF7_QulrJGXQdACHRhbzcPXfRvYp-MMcscjPIFvHtqNECeiS2yut6_gpfCSaU=w720-h310" alt="Screenshot">
<img src="https://lh3.googleusercontent.com/RMe53Fkb5iH93OE9CZZxVXuTocwwo7VeP2S3onHENMNHm_dKGNvgyUisqaQcidpHoQ=w720-h310" alt="Screenshot">
<img src="https://lh3.googleusercontent.com/BxuV0mE-oybSzwigqtEapss83Y3vHaoWE30tC_Q0zgNkNlFUYBn3ioCRXc3NuwIRzkIs=w720-h310" alt="Screenshot">
<img src="https://lh3.googleusercontent.com/kVKqHaJtOq1eMnWDBwXnZ70b43z1OelkHsglfL0zNfusD9RXRtzUegXpGHP2-iQ3Zw=w720-h310" alt="Screenshot">
<img src="https://lh3.googleusercontent.com/RZtXvv4lm2cwE86_TH_pc8WYrt0FHd_H0qgAtDz2XiZK_O7HIR5fuRozhZdN0P2lZuFi=w720-h310" alt="Screenshot">

## Features

* Organize your notes into libraries with different colors and icons
* Create and manage your notes easily
* Dark Mode
* Ad-Free
* Archive Notes 
* List and Grid layout modes
* Reminders
* Auto Save
* Minimal Design

## Libraries

* Kotlin
* Android Architecture Components(LiveData, ViewModel, etc...)
* Data Binding
* Room (DB)
* Koin (DI)

## Architecture

> The app uses Clean Architecture with MVVM. It's splitted into 3 Main Layers. 

#### Domain
###### Contains all the Business Logic. It's splitted into 3 packages.

* ##### Intreactor
    Contains Use Cases for each action that can be triggered and used directly through ViewModels in the `Presentation` Layer.

* ##### Model
    Contains app Models.

* ##### Repository 
    Contains repositories interfaces which are used by the Use Cases and implemented in the `Data` Layer. 
    
#### Data
######  Contains repositories implementations and Data Sources interfaces. It's splitted into 2 pacakges. 

* ##### Local
    Contains interfaces to preform actions locally and implementend in the Local Module.

#### Presentation (app)
######  Contains all the UI Logic.

#### DI (Dependency Injection)
###### Contains Koin DI modules.

#### BuildSrc
###### Contains Gradle dependencies and app configuration.

## Requirements

* JDK 1.8
* [Android SDK](https://developer.android.com/studio/index.html)
* Android L (API 21)
* Latest Android SDK Tools and build tools.

## Running

```
./gradlew
```

## Tests
Tests will be added soon!

## License
Noto is distributed under the terms of the Apache License (Version 2.0). See [License](LICENSE.md) for details.
