# Water Quality
An Android mobile application that determines the concentration of a given chemical in a sample of water through image processing techniques. Used in conjunction with lab cards developed specifically for this purpose. Built to interact with a MySQL database and Apache web servers.

# Authors
Nasser Alhamadah
Connor Berg
Jacob Kostner

# Notes
A config.json file should be created in public_html/ with the database name, hostname, username, password, and a secret used for encrypting user passwords.
This information should be stored in the following format:
{
  "host": "hostname",
  "db": "database",
  "user": "username",
  "pass": "password",
  "secret": "asdfasdfasdflkjlkjlkj asdfaoweiovmawoei asdlfkjawoeijao"
}

A live version of the website is currently hosted at https://wq.cs.ksu.edu.
