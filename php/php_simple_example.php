<?php

/*
 * Copyright (c) 2016 ObjectLabs Corporation
 * Distributed under the MIT license - http://opensource.org/licenses/MIT
 *
 * Written with extension mongo 1.6.12
 * Documentation: http://php.net/mongo
 * A PHP script connecting to a MongoDB database given a MongoDB Connection URI.
 */

/*
 * Standard single-node URI format: 
 * mongodb://[username:password@]host:port/[database]
 */
$uri = getenv('MONGODB_URI');
if ($uri == null) {
   $uri = "mongodb://user:pass@host:port/db";
}

echo "Connecting to $uri\n";

$client = new MongoDB\Driver\Manager($uri);

/*
 * First we'll add a few songs. Nothing is required to create the songs
 * collection; it is created automatically when we insert.
 */
echo "Executing bulk write\n";
$bulk = new MongoDB\Driver\BulkWrite;
$bulk->insert(['decade' => '1970s', 'artist' => 'Debby Boone', 'song' => 'You Light Up My Life',  'weeksAtOne' => 10]);
$bulk->insert(['decade' => '1980s', 'artist' => 'Olivia Newton-John', 'song' => 'Physical',  'weeksAtOne' => 10]);
$bulk->insert(['decade' => '1990s', 'artist' => 'Mariah Carey', 'song' => 'One Sweet Day',  'weeksAtOne' => 16]);
$client->executeBulkWrite("db.songs", $bulk);

/*
 * Then we need to give Boyz II Men credit for their contribution to
 * the hit "One Sweet Day".
*/

echo "Executing update\n";
$bulk = new MongoDB\Driver\BulkWrite;
$bulk->update(
    array('artist' => 'Mariah Carey'), 
    array('$set' => array('artist' => 'Mariah Carey ft. Boyz II Men'))
);
$client->executeBulkWrite("db.songs", $bulk);
    
/*
 * Finally we run a query which returns all the hits that spent 10 
 * or more weeks at number 1. 
*/
echo "Executing query\n";
$query = new MongoDB\Driver\Query(['weeksAtOne' => ['$gte' => 10]]);
$cursor = $client->executeQuery("db.songs", $query);
$queryResults = $cursor->toArray();
var_dump($queryResults);

// Since this is an example, we'll clean up after ourselves.
echo "Executing dropCollection\n";
$dropCollection = new MongoDB\Driver\Command(["drop" => "songs"]);
$client->executeCommand("db", $dropCollection);

?>
