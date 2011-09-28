#!/usr/bin/perl

$numArgs = @ARGV;
print "Number of arguments ".$numArgs."\n";
  
open(EVENT_NAMES, '<',"events.names".$eventnames_file) || die("Could not open event descriptors file ");

EVENT_DESCRIPTORS: while (<EVENT_NAMES>) {
  @eventname_and_properties = split /,/;
    
  $num_properties = @eventname_and_properties - 1;
  next EVENT_DESCRIPTORS if ( $num_properties eq 0 ); #skip events without properties 

  $eventname = $eventname_and_properties[0];
    
  open(EVENT_CSV_FILE, '>', $eventname.".csv") || die("Could not open event csv file ".$eventname);
  print $eventname. "\n";
    
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