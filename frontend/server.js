const path = require('path');
const express = require('express');
const app = express();

app.use(express.static(path.join(__dirname, './dist/angular_template')));

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, './dist/angular_template', 'index.html'));
});

app.listen(80, () => {
  console.log('SERVER RUNNING');
});
