#!/usr/bin/perl
use Time::Local 'timelocal';
chomp ($sedata_fodler = $ARGV[0]); 
chomp ($comment_header_row = $ARGV[1]); 

if (length($sedata_fodler) == 0) { die "Missing events data folder!"; }
 
my $line = 0;
my %hashTable = ();
 
my $min_epoch_seconds = 0;
 
@files_with_events = ();
 
@files = <*.csv>;

foreach $file (@files) {
  print $file . "\n";
  $line = 0;
  open(RAW, '<',$file) || die("Could not open file!");
  @raw_data=<RAW>; 
  LINE: foreach $event_line (@raw_data) {
    $line++;
    @properties = parse_csv($event_line);
    next LINE if ( $line eq 1 ) ; #skip header
        
    $timemark = $properties[0];
    print $timemark."\n";

    $epoch_seconds = get_epoch_seconds_from_timemark($timemark);
    print " Epoch seconds =".$epoch_seconds."\n";
    if ($min_epoch_seconds eq 0) {
      $min_epoch_seconds = $epoch_seconds;
    } elsif ($epoch_seconds < $min_epoch_seconds) {
      $min_epoch_seconds = $epoch_seconds;
    }
  }
    
  if ($line > 1) {
    push(@files_with_events, $file); 
  } 
  close(RAW); 
} 

print " MIN Epoch seconds =".$min_epoch_seconds."\n";
 
foreach $raw_file (@files_with_events) {
  open(RAW, '<',$raw_file) || die("Could not open raw file!");
  $dir = "../".$sedata_fodler;
  unless (-d $dir) {
    mkdir $dir || die "Could not create events data folder specified as ".$sedata_fodler;
  }
   
  open(TIMESTAMPED, '>',$dir."/".$raw_file) || die("Could not open timestamped file!");
  @raw_data=<RAW>; 

  $line = 0;
  LINE: foreach $event_line (@raw_data) {
    @properties = parse_csv($event_line);
    if ( $line eq 0 ) {
        if ($comment_header_row eq "comment_header_row") {
            print TIMESTAMPED "#".$event_line;
        } else {
            #write header as is
            print TIMESTAMPED $event_line; 
        }
    } else {
      $timemark = $properties[0];
      $epoch_seconds = get_epoch_seconds_from_timemark($timemark);
      $difference = $epoch_seconds - $min_epoch_seconds;
      #append timestamp to event line
      $event_line =~ s/\r|\n//g;
      print TIMESTAMPED $event_line.",".$difference."\n"; 
    }
        
    $line++;
  } 
  close(RAW); 
  close(TIMESTAMPED); 
}


sub parse_csv {
  my $text = shift;
  my @new  = ();
  push( @new, $+ ) while $text =~ m{
       "([^\"\\]*(?:\\.[^\"\\]*)*)",?
           |  ([^,]+),?
           | ,
       }gx;
  push( @new, undef ) if substr( $text, -1, 1 ) eq ',';
  return @new;
}

sub get_epoch_seconds_from_timemark() {
  my $timemark = shift;

  ($hh, $min, $dd, $month, $yyyy) = ($timemark =~ /(\d+):(\d+)\s(\d+)\.(\d+)\.(\d+)/);
  print "Hour=".$hh." Minute=".$min." Day=".$dd." Month=".$month." Year=".$yyyy."\n";
  # calculate epoch seconds at midnight on that day in this timezone
  if ($hh eq "" || $min eq "" ) {
    return 0;
  }
  $epoch_seconds = timelocal(0, $min, $hh, $dd, $month-1, $yyyy);
  return $epoch_seconds;
}