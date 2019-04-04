version=2
#Prefix with date, time, milliseconds in VMS transformer log style
prefix=%date1:date-iso% %time1:time-24hr%,%ms1:number%
rule=:%-:whitespace%%lvl:word%%-:whitespace%%cls:word%%-:whitespace%%thread:string{"quoting.char.begin":"[", "quoting.char.end":"]"}%%-:whitespace%%method:string{"quoting.char.begin":"[", "quoting.char.end":"]"}%%-:whitespace%%msg:rest%

# Matches all. Needs to come after custom parsing. For non-matching ones.
prefix=
rule=:%msg:rest%

