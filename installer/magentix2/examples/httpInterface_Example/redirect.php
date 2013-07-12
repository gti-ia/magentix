<?php
require_once 'HTTP/Request2.php';
$xhrContent = file_get_contents('php://input');
// specify the address where the HTTP interface is running. In this example it is running in our localhost
$request = new HTTP_Request2('http://localhost:8082', HTTP_Request2::METHOD_POST);
$request->setBody($xhrContent);
try {
   $response = $request->send();
   if (200 == $response->getStatus()) {
       echo $response->getBody();
   } else {
       echo $response->getStatus() . ' ' . $response->getReasonPhrase() . ' - ' . $response->getBody();
   }
} catch (HTTP_Request2_Exception $e) {
   echo 'Error: ' . $e->getMessage();
}

?>
