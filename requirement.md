page 1 (user selects role)

two buttons one is are you a developer and second is are you a tester 

if user click on developer button then it will go to developer page

if user click on tester button then it will go to tester page

page 2 (login)

if user is chooses devloper then user will login with credentials if he already has an developer accounf other wise there will be a sign up button to create a new developer account

if user chooses tester then user will login with credentials if he already has an tester accounf other wise there will be a sign up button to create a new tester account

user cannot login to developer account with tester credentials and vice versa

at the time of signup the role should be automatically assigned to the user account while user creation 

page 3 (developer dashboard)

developer dashboard screen should show
1.app upload screen where user can upload app name , app icon (optional), app version , app description (optional), app closed testing link and payment option , developer can upload multiple apps so on dashboard it will be list of uploaded apps and on each item click it should go to the deatiled status page of the app , the detailed page should show the status and other info of the testing and how many testers have installed their app. add app details screen should be a diffferent screen.

page 3 (tester dashbaord)
1.tester dashboard should show list of apps available for testing and on each item click it should go to the deatiled status page of the app , the detailed page should show the information about the app and if the user has installed the app then it should show the status of the testing and other info of the testing and how many days are remaining for the testing and if the user has not installed the app then it should show the opt in button , on click of opt in they will be added to the particular app testing list and they will be notified when the app is updated and approved by the developer and they can install the updated app and continue the testing for 15 days. for each app they will get some money , the money should be credited to their account after the testing period is completed and they have installed the app and continued the testing for 15 days. the amount for each app is not fixed , devloper can choose how much they want to pay , base price will be 399 in which it should be distributed equally among the testers and some commision to us , for ex if developer sets the bid amount to 650 15 testers opted in then 600 should be distributed equally among the testers and 50 should be our commision , if less than 15 testers opt in then the amount should be distributed equally among the testers and our commision should be increased proportionally , for ex if 10 testers opt in then 600 should be distributed equally among the 10 testers and 50 should be our commision , if 5 testers opt in then 600 should be distributed equally among the 5 testers and 50 should be our commision , if 1 tester opt in then 600 should be given to that tester and 50 should be our commision , if no tester opt in then the developer should get a refund of the amount he paid , the refund should be credited to his account within 48 hours. and maximum 20 testers can opt in or if developer has set the the number of testers in the app details then only that number of testers can opt in. testers will get paid only if they have installed the app for minimum 15 days.

payment by devloper and payment to testers we will implement it at the last because thats need razorpay setup.

notes

1. developer will upload app details and there should be a time period for testers to opt in. when the required amount of testers opt in then developer will be able to see these testers opted in and he can add the emails in the playstore and enable the closed testing link and then come back and open the testing , for testers they will opt in and wait for the developer to start the test , once the status of the app is start test then testers can download the app and link will be avaialble for them , link should be read only until the developer starts it. 

2.for all these think of the model classes for tester account , developer account , app details model which will have the app details , plus list of testers opted, status of the app like start pending , started , 1 tester installed , n number of testers have installed for n number of days. also bugs can be added by testers and developer can see the bugs and can mark them as resolved and can reply to the testers for the bugs. 

3. all the authentication should be done with our backend and remaining all the api's should be our's only, suggest me if i should setup a raspberry pie for running the server 

4.app theme should be shades of white for light mode and shades of black for dark mode and primary color should be blue

5.whatever i have explained you can add or remove based on what is possible what is not and create a todolist for all these 

6.later we will add sign in with google and razorpay for payments so keep that in mind 

7.whatever documentation you will create for my requirement make it AI friendly so that gemini , claude and gpt all can easily understand what exactly needs to be done and where to start