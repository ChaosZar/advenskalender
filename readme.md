# Description
A simple Application which pushes a set of pictures to an Discord Text-Channel.

A CRON-Task is triggered every hour which checks what files should be posted.

## Features
- Timed execution (hourly)
- pictures of previous days will be posted as well as the current day
- pictures will be deleted after they have been posted
- last picture will have Emojis to vote (Even if voting is not possible)
- night mode - between 20:00 and 10:00 nothing will be posted, to respect your friends.

Missing:
- Metatdata which will be send in addition to the files.

# Resource Format
Resource Directory most be in format:

{root}/{DayOfMonth}/{n}.jpg

{root}/{DayOfMonth}/{n+1}.jpg

{root}/{DayOfMonth}/{n+x}.jpg

example:
/home/user/calendar/01/01.jpg

/home/user/calendar/01/02.jpg

/home/user/calendar/01/03.jpg

/home/user/calendar/02/01.jpg

/home/user/calendar/02/02.jpg
