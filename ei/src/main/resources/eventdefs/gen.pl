#!/usr/bin/perl

$numArgs = @ARGV;
print "Number of arguments ".$numArgs."\n";

$event_desriptors_file="events.names";
open(DAT, $event_desriptors_file) || die("Could not open event desriptors file for reading!".$event_desriptors_file);
@raw_event_descriptors =<DAT>; 
close(DAT); 
  
open(NEW_DAT, '>', $event_desriptors_file) || die("Could not open event desriptors file for writing ".$event_desriptors_file);

foreach $event_descriptor (@raw_event_descriptors) 
{
  $_ = $event_descriptor;
  print "ensuring timestamp field is present in ".$event_descriptor."\n";	
  if (/long timestamp/) {
      print NEW_DAT ;
  } else {
      ~s/\s+$//;
      print NEW_DAT;
      print NEW_DAT ",long timestamp$/";
  }
}

close(NEW_DAT);
  
open(EVENT_NAMES, '<',$event_desriptors_file) || die("Could not open event descriptors file for reading ");

EVENT_DESCRIPTORS: while (<EVENT_NAMES>) {
  @eventname_and_properties = split /,/;
    
  $num_properties = @eventname_and_properties - 1;
  next EVENT_DESCRIPTORS if ( $num_properties eq 0 ); #skip events without properties 

  $eventname = $eventname_and_properties[0];
    
  open(EVENT_CSV_FILE, '>', $eventname.".csv") || die("Could not open event csv file ".$eventname);
  print "generated ".$eventname. "\n";
    
  if ($numArgs > 0) {
    if ($ARGV[0] eq "comment_header_row") {
      print EVENT_CSV_FILE "#";
    }
  }
    
  for my $i (1 .. $num_properties) {
    $_ = $eventname_and_properties[$i];
    ~s/\s+$//;
    print EVENT_CSV_FILE $_;
          
    if ($i < $num_properties) {
      print EVENT_CSV_FILE ",";
    }
  }
  close(EVENT_CSV_FILE); 
}
 
close(EVENT_NAMES); 
