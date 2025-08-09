insert into FactTitlesAuthors(TitleKey, AuthorKey, AuthorOrder)
Select
    [TitleKey] = DimTitles.TitleKey
--, title_id
     , [AuthorKey] = DimAuthors.AuthorKey
--, au_id
     , [AuthorOrder] = au_ord
From pubs.dbo.titleauthor
         JOIN DWPubsSales.dbo.DimTitles
              On pubs.dbo.titleauthor.Title_id = DWPubsSales.dbo.DimTitles.TitleId
         JOIN DWPubsSales.dbo.DimAuthors
              On pubs.dbo.titleauthor.Au_id = DWPubsSales.dbo.DimAuthors.AuthorId
