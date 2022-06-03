# BoardGameCollector
The goal of this project was to create an application which stores user's games, game's extensions and creates rank history.
App cooperates with service boardgamegeek.com, it takes the data from their API about user's collections and saves it to the database.

## Required functions

- Create a responsive layout, <br>
- Collect data about user's collection, <br>
- Saving data to SQLite database, <br>
- Using boardgamegeek's service api to get the data and parse xml file,  <br>
- Games sorting mechanism via name, rank, year <br>
- Create history game ranking.  <br>

## How to use :
At first, you have to create an account in boardgamegeek.com and add any games/extensions to your collection, after opening application you will be able to write you service's username.
If the name is correct, you have to press synchronisation button which is on right bottom corner. You can also clear the data by pressing left bottom button.
After synchronisation, to check your collection you will be able to see your games after pressing "show my boardgames".
In this section you can show extensions, games or both. You can sort your collections by name, release date or current rank.
Some games and all extensions are unranked. You can see game's rank history after clicked any game row.


<p align="center">
<img width="300px" src="https://user-images.githubusercontent.com/81914576/171814816-686c2b5b-deda-4e32-a439-a40486447b95.jpg">
<img width="300px" src="https://user-images.githubusercontent.com/81914576/171814817-399934c7-6f8b-46b9-b966-d35dd95d3032.jpg">
<img width="300px" src="https://user-images.githubusercontent.com/81914576/171814819-c303250b-c1a7-478b-97c8-5877da44e48a.jpg">
<img width="300px" src="https://user-images.githubusercontent.com/81914576/171814823-f78f8dba-d17b-4ce4-8be9-5b8ff609a23d.jpg">
<img src="https://user-images.githubusercontent.com/81914576/171814820-037bd977-9cb7-4650-b2db-1e22210b1fc5.jpg">
<img src="https://user-images.githubusercontent.com/81914576/171814822-7a7a2d5a-9b7b-457e-86d5-eb392496f46f.jpg">
<img width="300px" src="https://user-images.githubusercontent.com/81914576/171814825-83faaf60-4f7d-432f-a633-bc3fc8f318d4.jpg">
</p>
