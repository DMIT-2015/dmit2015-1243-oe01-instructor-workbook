insert into DimAuthors(AuthorId, AuthorName, AuthorState)
Select
    [AuthorId] = Cast( au_id as nChar(11) )
     , [AuthorName] = Cast( (au_fname + ' ' + au_lname) as nVarchar(100) )
     , [AuthorState] = Cast( state as nChar(2) )
From pubs.dbo.authors