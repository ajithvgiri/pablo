# Pablo
Pablo is simple and easy to use image loading library for android

# Setup
## 1. Provide the gradle dependency

Add it in your root build.gradle at the end of repositories:
```gradle
	allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the gradle dependency to your `app` module `build.gradle` file:

``` gradle
	dependencies {
	   implementation 'com.github.ajithvgiri:pablo:v0.0.1'
	}

```

## 2. Initialization of the Pablo library

#### Java
``` java
    Pablo pablo = new Pablo(context);
    pablo.setCompress(false); // default value is true
    pablo.displayImage("imageUrl", imageView)
```

#### Kotlin
``` kotlin
    val pablo = Pablo(context)
    pablo.isCompress = false 
    pablo.displayImage(imageUrl, fullscreen_imageView)
```
## Screen Shots

![Pablo](https://i.imgur.com/mRGG50I.jpg)
