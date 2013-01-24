<?php
$param=$_GET['filename'];
header('Content-disposition: attachment; filename='+$param);
header('Content-type: application/x-unknown');
readfile($param);
?>
