{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = [
    pkgs.scala
    pkgs.sbt
    pkgs.chromedriver
    pkgs.jdk11
  ];

  shellHook = ''
    echo "Nix shell environment with Scala, SBT, ChromeDriver, and JDK 11 is ready."
    echo "Don't forget to replace /path/to/chromedriver in your Scala script with the actual path."
  '';
}
