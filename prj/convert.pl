#!/usr/bin/perl

use strict;

my $file = $ARGV[0];

my $newFile = $file.".csv";

open(FH,"<$file") or die "fehler beim Lesen\n";
my $content = <FH>;
$content =~ s/\t|\n|\r//sgi;

my @lines;
for (my $i=0;$i<length($content);$i+=54)
{
  my $bla = substr($content,$i,54);
  my $foo1 = substr($bla,0,4);
  my $foo2 = substr($bla,4);
  push(@lines,$foo1.";".$foo2);
}
close(FH);

for (@lines) {
  chomp();
  print $_."\n";
}
