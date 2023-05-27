$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("file:src/test/java/Features/Assignment.feature");
formatter.feature({
  "name": "Signup and send mail testing",
  "description": "",
  "keyword": "Feature"
});
formatter.scenario({
  "name": "Signup",
  "description": "",
  "keyword": "Scenario",
  "tags": [
    {
      "name": "@assign"
    }
  ]
});
formatter.before({
  "status": "passed"
});
formatter.step({
  "name": "User logs in the elearning portal",
  "keyword": "Given "
});
formatter.match({
  "location": "Assignmentstepdefinations.user_logs_in_the_elearning_portal()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "User clicks Sign Up",
  "keyword": "When "
});
formatter.match({
  "location": "Assignmentstepdefinations.user_clicks_Sign_Up()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "User gives all details and clicks Register",
  "keyword": "Then "
});
formatter.match({
  "location": "Assignmentstepdefinations.user_gives_all_details_and_clicks_Register()"
});
formatter.result({
  "status": "passed"
});
formatter.after({
  "status": "passed"
});
formatter.scenario({
  "name": "Send mail",
  "description": "",
  "keyword": "Scenario",
  "tags": [
    {
      "name": "@assign"
    }
  ]
});
formatter.before({
  "status": "passed"
});
formatter.step({
  "name": "User logs in the elearning portal",
  "keyword": "Given "
});
formatter.match({
  "location": "Assignmentstepdefinations.user_logs_in_the_elearning_portal()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "User gives Username password and clicks login",
  "keyword": "When "
});
formatter.match({
  "location": "Assignmentstepdefinations.user_gives_Username_password_and_clicks_login()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "User clicks compose give all details and clicks send message button",
  "keyword": "And "
});
formatter.match({
  "location": "Assignmentstepdefinations.user_clicks_compose_give_all_details_and_clicks_send_message_button()"
});
formatter.result({
  "status": "passed"
});
formatter.step({
  "name": "User should get a sucessful sent message",
  "keyword": "Then "
});
formatter.match({
  "location": "Assignmentstepdefinations.user_should_get_a_sucessful_sent_message()"
});
formatter.result({
  "status": "passed"
});
formatter.after({
  "status": "passed"
});
});