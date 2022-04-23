# PalesNewsApp
- PalesNews is an MVVM clean architecture app built for android platform.
- fetches news from an API https://newsapi.org/ and cache the results then show them in a RecyclerView.
- supports offline news browsing.
- supports paging.
- supports searching news.
- supports news from 4 countries.

# Preview
![Untitled-1-Recovered](https://user-images.githubusercontent.com/78867217/164836681-17bea3f0-f550-48da-b4c2-18a43ea25cb1.jpg)

# Libraries And Technoligies used
- MVVM + LiveData : Android architecture used to saperate logic code from ui and save the application state in case the configuration changes.
- Retrofit + Gson Converter : Fetch news from rest api as a gson file and convert it to a kotlin object.
- Room : Save the articles into a local database.
- Coroutines : Executing some code in the background.
- Dagger hilt : Dependency injection.
- Navigation Component : Navigate between fragments.
- KTX : Share the viewModel between many fragments.
- Glide : Catch and cache images from the internet and show them in an imageView.
- Swipe to refresh : Swiping up to refresh the news.
- viewBinding : to access the views without needing to infalte them.
