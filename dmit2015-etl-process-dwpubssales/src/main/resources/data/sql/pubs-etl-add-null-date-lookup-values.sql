Set Identity_Insert [DWPubsSales].[dbo].[DimDates] On
Insert Into [DWPubsSales].[dbo].[DimDates]
( [DateKey]
, [Date]
, [DateName]
, [Month]
, [MonthName]
, [Quarter]
, [QuarterName]
, [Year], [YearName] )
Select
    [DateKey] = -1
     , [Date] =  Cast('01/01/1900' as nVarchar(50) )
     , [DateName] = Cast('Unknown Day' as nVarchar(50) )
     , [Month] = -1
     , [MonthName] = Cast('Unknown Month' as nVarchar(50) )
     , [Quarter] =  -1
     , [QuarterName] = Cast('Unknown Quarter' as nVarchar(50) )
     , [Year] = -1
     , [YearName] = Cast('Unknown Year' as nVarchar(50) )
Union
Select
    [DateKey] = -2
     , [Date] = Cast('01/01/1900' as nVarchar(50) )
     , [DateName] = Cast('Corrupt Day' as nVarchar(50) )
     , [Month] = -2
     , [MonthName] = Cast('Corrupt Month' as nVarchar(50) )
     , [Quarter] =  -2
     , [QuarterName] = Cast('Corrupt Quarter' as nVarchar(50) )
     , [Year] = -2
     , [YearName] = Cast('Corrupt Year' as nVarchar(50) )
Set Identity_Insert [DWPubsSales].[dbo].[DimDates] Off
