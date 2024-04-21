config = {
  _id: "chd",
  configsvr: true,
  members: [
    {_id: 0, host: "hd1:27019", priority: 1},
    {_id: 1, host: "hd2:27019", priority: 2},
    {_id: 2, host: "hd3:27019", priority: 3},
  ]
};
rs.initiate(config)
