# Jemmy Swing UI test automation library

Jemmy is a Java library for automating and testing Swing and AWT user
interfaces. It finds components, wraps them in operators, drives keyboard and
mouse interaction, and waits for observable UI state.

This repository began as a fork of NetBeans Jemmy 2.2.7.5, downloaded
around 2007 from revision 19 of the former java.net Subversion repository
at `https://jemmy.dev.java.net/svn/jemmy/trunk/Jemmy2`.

It has since diverged substantially.

The current OpenJDK continuation,
[openjdk/jemmy-v2](https://github.com/openjdk/jemmy-v2), was compared with
this fork and their shared Jemmy 2.2.7.5 ancestor to identify and port
upstream fixes and improvements missing here. The OpenJDK reference point
for that comparison was commit
[`6208363`](https://github.com/openjdk/jemmy-v2/commit/620836372c637f5eb36b1a5333df92eb698ed191).
That reconciliation and subsequent development took place during the summer
of 2026 with AI assistance.
