Feature: Send one way messages
  As a user
  I want to be able to use REST API to send messages
  So that I can demonstrate the new SQS library capabilities

  Scenario: Sending message through queue
    When server endpoint is called with a simple message:
      | title   | content      |
      | title 1 | content 1234 |
    Then a new message should be queued with title "title 1" and content "content 1234"

  Scenario: Sending counter through queue
    When server count endpoint is called:
      | message 1 |
      | message 2 |
      | message 3 |
      | message 4 |
    Then the following counter messages should be queued:
      | data      | number |
      | message 1 | 1      |
      | message 2 | 2      |
      | message 3 | 3      |
      | message 4 | 4      |