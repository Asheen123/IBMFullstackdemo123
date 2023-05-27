Feature: Signup and send mail testing

@assign
 Scenario: Signup
    Given User logs in the elearning portal
    When User clicks Sign Up
    Then User gives all details and clicks Register
    
  @assign  
    Scenario: Send mail
    Given User logs in the elearning portal
    When User gives Username password and clicks login
    And User clicks compose give all details and clicks send message button
    Then User should get a sucessful sent message