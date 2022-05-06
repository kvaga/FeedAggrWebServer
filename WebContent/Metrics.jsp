<%@page import="java.text.SimpleDateFormat,java.util.Date"%><%
final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
int max = 100;
new java.util.Date();
int min = 0;
int range = max - min + 1;
int rand = (int)(Math.random() * range) + min;
%>
FeedsUpdateJob SuccessCount=<%= rand %>,FailedCount=0
CompositeFeedsUpdateJob SuccessCount=<%= rand %>,FailedCount=0