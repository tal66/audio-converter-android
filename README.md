### Audio Converter Android App

Record audio and convert it to text and/or image.

- 3 screens (fragments):
  - list of audio files and recordings (RecyclerView)
  - audio recorder
  - audio conversion (results via polling) and audio player

Repo includes only the android side. Backend is on AWS, and external URL's (responsible for the results) are not in the repo.

### A bit of android specifics:

In android, **Fragments** are components of the UI, and **ViewModels** hold the business logic and state.
Fragments are used in **Activities** (windows/screens), in this case, the app uses a single Activity and 3 Fragments:

- FilesListFragment - displays a **list of files** using a **RecyclerView** (reuses elements when scrolling), 
and allows users to switch between different file providers (music files and recordings). 
The fragment utilizes the FilesListViewModel to manage data and interactions, and navigates to a file info screen when a file in the list is clicked.

  
- RecorderFragment - **audio recorder**. Includes buttons to start and stop recording, 
and a chronometer to display the recording duration. 
Uses the RecorderViewModel to manage the recording process.


- FileInfoFragment - displays information about a selected file. Includes functionality 
for playing audio files, and **displaying results** from the text and image APIs (using other ViewModels).
**LiveData** is utilized to communicate the status of audio-to-text operations and other operations.
  (In android, *LiveData* allows UI components to observe changes in the data, and it is used throughout the app.)

  
UI: the default theme is used for the app (no styling preferences). \
The app was tested on Android v10. \
Written ~01-02/2023.

![Alt text](img/image.png)


