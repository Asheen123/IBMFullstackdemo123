'use strict';

const express = require('express');
const PORT = 5010;
const app = express();
const { v4: uuidv4 } = require('uuid');

app.get('/oauthtemptoken',(req,res)=> {
  res.status(200).send(uuidv4());
});

app.listen(PORT, function() {
    console.log('Express server listening on port ' + PORT);
});