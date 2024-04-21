config = {
  _id: "hd",
  members: [
    {_id: 0, host: "hd1:27018", priority: 1},
    {_id: 1, host: "hd2:27018", priority: 2},
    {_id: 2, host: "hd3:27018", priority: 3},
  ]
};
rs.initiate(config)