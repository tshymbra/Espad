 #!/usr/bin/perl
  
$event_desriptors_file="events.names";
open(DAT, $event_desriptors_file) || die("Could not open event desriptors file for reading!".$event_desriptors_file);
@raw_event_descriptors =<DAT>; 
close(DAT); 
  
open(NEW_DAT, '>', $event_desriptors_file) || die("Could not open event desriptors file for writing ".$event_desriptors_file);

foreach $event_descriptor (@raw_event_descriptors) 
{
  $_ = $event_descriptor;
  if (/long timestamp/) {
      print NEW_DAT ;
  } else {
      ~s/\s+$//;
      print NEW_DAT;
      print NEW_DAT ",long timestamp$/";
  }
}

close(NEW_DAT);