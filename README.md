# Music Practice and Setlist Companion

An Android app for the Android Development course at USJ (teacher: Toufic Fakhry), by Hadi Joe Bassil and Rami.

Musicians usually keep their songs, practice notes and setlists in different places: a notebook, some messages, a spreadsheet. We wanted one app on the phone where you save your songs and build setlists from them.

This repo is the progress version for the mid-project evaluation. It has the feature we demo, not the whole final app.

## What works now

You can save songs with a title, a key, a difficulty (Easy, Medium, Hard) and a status (Learning, Practicing, Ready). You can create setlists and add your saved songs to them. Inside a setlist you can remove a song or move it up or down. Everything is stored on the phone with Room, so it is still there after closing the app. There is no API and the app does not need internet.

## Running it

Open the project folder in Android Studio (the one that contains `settings.gradle.kts`), wait for the Gradle sync, then run the `app` configuration on an emulator or a phone.

- The minimum Android version is 13 (API 33), so the emulator or phone needs to be 13 or newer.
- The first sync downloads the dependencies, so you need internet for that.
- The project uses Android Gradle Plugin 9.4.1 (see `gradle/libs.versions.toml`). If your Android Studio is old, update it.

To check that it works: add two or three songs, create a setlist, open it, add the songs and change their order with Up and Down. Then close the app completely and open it again. The songs, the setlist and the order should still be there.

## How it is built

The app has two tabs, Songs and Setlists. Tapping a setlist opens the Setlist detail screen, which has a Back button. Navigation is done with Navigation Compose and the routes are in `AppNavigation.kt`.

Each screen has a ViewModel that exposes its state as a `StateFlow`. The ViewModel talks to a Repository, the Repository uses a DAO, and the DAO reads and writes the Room database. The DAO queries return a `Flow`, so when the data changes the screen is redrawn without us doing anything. The UI only shows the state and sends actions up to the ViewModel.

There are three tables:

| table | columns |
|---|---|
| `songs` | `id`, `title`, `songKey`, `difficulty`, `status` |
| `setlists` | `id`, `name` |
| `setlist_songs` | `setlistId`, `songId`, `position` |

A setlist has many songs and a song can be in many setlists, so `setlist_songs` is a link table between them. Its primary key is the pair `setlistId` + `songId`, which means the same song can't be added twice to one setlist. The order of a setlist is the `position` column. Moving a song up or down swaps its position with the neighbour's, inside one transaction, and the list is read with `ORDER BY position`.

The code is in `app/src/main/java/com/hadijoe/projectandroid/`:

```
data/            entities, DAOs, repositories, the database
model/           the Song and Setlist classes the UI uses
ui/songs/        Songs screen, add-song form, SongsViewModel
ui/setlists/     Setlists screen, Setlist detail screen, their ViewModels
ui/navigation/   routes and tabs
ui/theme/        colors and typography
```

We only use Kotlin, Jetpack Compose, ViewModel, Room, Flow and Navigation Compose.

## Who did what

Hadi did the Songs screen. Rami did the Setlists screen and the Setlist detail screen.

## Still to do

The database is built with `fallbackToDestructiveMigration`. That is fine while we are still changing the tables, but it deletes the saved data when the database version changes, so for the final project we want to replace it with proper Room migrations. We also still need to test the order after force-closing the app, and clean up errors and small details.

Not planned: music streaming, lyrics, a social feed, real-time collaboration between band members.
