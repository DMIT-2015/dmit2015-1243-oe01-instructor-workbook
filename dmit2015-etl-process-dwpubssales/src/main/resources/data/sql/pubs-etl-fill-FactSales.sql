insert into FactSales(OrderNumber, OrderDateKey, TitleKey, StoreKey, SalesQuantity)
Select
    [OrderNumber] = Cast(ord_num as nVarchar(50))
     , [OrderDateKey] = DateKey
--, title_id
     , [TitleKey] = DimTitles.TitleKey
--, stor_id
     , [StoreKey] = DimStores.StoreKey
     , [SalesQuantity] = qty
From pubs.dbo.sales
         JOIN DWPubsSales.dbo.DimDates
              On pubs.dbo.sales.ord_date = DWPubsSales.dbo.DimDates.date
         JOIN  DWPubsSales.dbo.DimTitles
               On pubs.dbo.sales.Title_id = DWPubsSales.dbo.DimTitles.TitleId
         JOIN  DWPubsSales.dbo.DimStores
               On pubs.dbo.sales.Stor_id = DWPubsSales.dbo.DimStores.StoreId