Feature: Facebook Testing

Background: 
  Given Login the Facebook using the URL
  
  Scenario: Facebook login
    # Given Login the Facebook using the URL
    When User gives his login credentails
    Then User should be able to login

  Scenario Outline: Multiple User login
  
   # Given Login the Facebook using the URL
    When User gives his "<username>" username and "<password>" password
    Then User should be able to login
    
    Examples:
    |username|password|
		|xyz|pw|
		|mny|pqr|