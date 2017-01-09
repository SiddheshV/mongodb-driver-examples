package main

import (
	"crypto/tls"
	"fmt"
        "gopkg.in/mgo.v2"
        "gopkg.in/mgo.v2/bson"
	"net"
	"os"
)

type Song struct {
	Decade	string
	Artist	string
	Song	string
	WeeksAtOne int
}

func main() {

	// Read mongodb connection string from environment variable
	uri := os.Getenv("MONGODB_URI")

	dialInfo, err := mgo.ParseURL(uri)
	if err != nil {
		panic(err)
	}

	dialInfo.DialServer = func(addr *mgo.ServerAddr) (net.Conn, error) {
		conn, err := tls.Dial("tcp", addr.String(), &tls.Config{})
		return conn, err
	}

	fmt.Printf("Connecting to %s\n", uri)
        session, err := mgo.DialWithInfo(dialInfo)
        if err != nil {
                panic(err)
        }
        defer session.Close()

        fmt.Printf("Executing bulk write\n")
        c := session.DB("db").C("songs")
        err = c.Insert(
		&Song{"1970s", "Debby Boone", "You Light Up My Life", 10},
		&Song{"1980s", "Olivia Newton-John", "Physical", 10},
		&Song{"1990s", "Mariah Carey", "One Sweet Day", 16})
        if err != nil {
                panic(err)
        }

	fmt.Printf("Executing query\n")
        var result []Song
        err = c.Find(
		  bson.M{
			"decade" : "1970s",
//			"WeeksAtOne": bson.M{
//			"$gte": 10,
//		  },
		}).All(&result)
	if err != nil {
		panic(err)
	}

        //fmt.Println("Result: ", result)
	fmt.Println(result)

	fmt.Printf("Executing dropCollection\n")
	c.DropCollection()
}
