<?php

/*
 * Json API.
 * 
 * USERNAME: fake
 * TOKEN: token 
 * // TOKEN: d985dfa98e44dda4cbd979affce1cf53
 * 
 * <HOSTNAME>/api.php?username=<user>&token=<api_token>&data=<data>
 * 
 * 
 * - data :
 *      The requested datas, the possibles values are : 
 *         all        Return all the informations (details & services).
 *         details    Return rbpi, uptime, memory, cpu, hdd, net, users informations (informations of the details page in raspcontrol).
 *         rbpi       Return the rbpi informations (hostname, distribution, kernel, firmware, internal & external ip).
 *         uptime     Return the uptime (D days H hours M minutes S seconds ).
 *         memory     Return the memory informations (ram, swap).
 *         cpu        Return the cpu informations (usage, heat).
 *         hdd        Return the hdd informations (array of disks).
 *         net        Return the net informations (number of connections, up & down).
 *         users      Return the array of ssh active users.
 *         services   Return the services with their status.
 */

header('Content-type: application/json');

class Secret {
  var $username;
  var $token;

  function __construct() {
    $this->username = 'fake';
    $this->token = 'token';
    //$this->token = 'd985dfa98e44dda4cbd979affce1cf53';
  }

  function verifyToken($username, $token) {
    if ($username != $this->username) {return false;}
    return $token == $this->token;
  }
}

$result = array();
// we think everything will be fine...
$result['code'] = 200;
$direct_output = false;

$s = new Secret();
  
if (!empty($_GET['username']) && !empty($_GET['token']) && $s->verifyToken($_GET['username'], $_GET['token'])){
  //Login is ok, building full api response
  if(!empty($_GET['data'])){

    define('UNICORN_PREFFIX_FILE', 'json'.DIRECTORY_SEPARATOR.'rbpi.api.');
    define('UNICORN_EXTENSION_FILE', '.json');

    function unicorn($file) {
      global $direct_output;
      $direct_output = true;
      return file_get_contents(UNICORN_PREFFIX_FILE. $file .UNICORN_EXTENSION_FILE);
    }

    switch($_GET['data']){
      case 'all':
        $result = unicorn('all');
      break;
      case 'rbpi':
        $result = unicorn('rbpi');
      break;
      case 'uptime':
        $result = unicorn('uptime');
      break;
      case 'memory':
        $result = unicorn('memory');
      break;
      case 'cpu':
        $result = unicorn('cpu');
      break;
      case 'hdd':
        $result = unicorn('hdd');
      break;
      case 'net':
        $result = unicorn('net');
      break;
      case 'users':
        $result = unicorn('users');
      break;
      case 'services':
        $result = unicorn('services');
      break;
      case 'details':
        $result = unicorn('details');
      break;
      default:
        // method not allowed
        $result['code'] = 405;
        $result['error'] = 'Incorrect data request.'; 
    }
  }
  else{
    // bad request
    $result['code'] = 400;
    $result['error'] = 'Empty data request.'; 
  }
}
else{
  // unauthorized
  $result['code'] = 401;
  $result['error'] = 'Incorrect username or API token.'; 
}

if ($direct_output) { exit($result); }
else { exit(json_encode($result)); }

?>
