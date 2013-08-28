Android Ask Library
===================

Installation
------------

First, clone the project and install:

    git clone git@github.com:askcs/android-ask-library.git
    cd android-ask-library
    mvn install

Then you should be able include the library from your own project's pom.xml as follows:

    <dependency>
        <groupId>com.askcs.android</groupId>
        <artifactId>android-ask-library</artifactId>
        <version>1.0-SNAPSHOT</version>
        <type>apklib</type>
    </dependency>



Overview
--------

The code is structured (ahem) in the following distinct packages:

### com.askcs.android.app
At the moment this contains only one class, `AskApplication`. You can extend
your own application class from this one, to easily reference components
related to database storage (local, sqlite) and connection to REST backend.

### com.askcs.android.sense
This contains some utilities involving Sense. The class in here which you
may need to access directly is `SenseApplication`. It extends `AskApplication`
to add easy referencing of Sense functionality.

### com.askcs.android.affectbutton
Contains an OpenGLES implementation of Joost Broekens' AffectButton.
You probably won't need to access anything inside this package, its
outer face is the widget `AffectButton` which lives in the
`com.askcs.android.widget` package.
To use it, include something like this in your layout XML:

    <com.askcs.android.widget.AffectButton
        android:id="@+id/affectbutton"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/transparent"
        android:padding="0dp" />


### com.askcs.android.appservices
Contains the classes that deal with communication with the app's REST backend.
At the moment of writing this library, there were two versions of the REST backend
actively used... It is kind of ugly, but for the moment this has been "resolved"
by extending the `RestClient` class (which knows about the old version) by the
`RestClient2` class (you guessed it). You have to specify which version your
app needs in the `res/strings.xml`. This is peculiar but convenient since it is
automagically inherited and/or overridable by dependent projects.

Your access point into this package should likely be via `AskApplication` or
 `SenseApplication`, see above, which will give you a handle to `AppServicesPlatform`
 which in turn switches to the right version, does calls asynchronously and routes
 the results back into your app. 

### com.askcs.android.data
Contains some classes to manage a local sqlite database, as well as methods to
get/set data from within the app. The main access point is the `Storage` class,
which extends the `SqlStorageBase` class.
TODO We should really only have the latter class in this package, the former
contains app-specific code! 

### com.askcs.android.gcm
Some code dealing with Google Cloud Messaging. It contains a broadcast receiver
that is able to forward notifications to your app -- determining if it is open
or not. You can then either catch the notification in the app, or leave it to
the general handler.

### com.askcs.android.json
Contains a single class `JsonMap` which maps a HashMap<String,String> to JSON.
TODO it should be easy to just use Jackson for this and get rid of this package.

### com.askcs.android.model
Contains a single class `Message`. This was written by Ian and I (Erik) am not
sure about this. I think it is Goalie-specific? 

### com.askcs.android.sherlock
Some utility classes to work with Sherlock Actionbar
TODO This can be deprecated since Google released their own compatibility lib
which includes actionbar support. I'm using that in StandBy and it works like a
charm.

### com.askcs.android.util
General-purpose utility package, contains most of our constants, easy access to
digest algorithm, preference keys and error codes.

### com.askcs.android.widget
Other than the already mentioned `AffectButton` this package contains mostly
'fontable' versions of standard android widgets such as `Button`, `EditText`,
`TextView`, `ToggleButton`. It also contains two helper classes to facilitate
these fontable widgets.




