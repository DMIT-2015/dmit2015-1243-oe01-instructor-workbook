insert into DimStores(StoreId, StoreName)
Select
    [StoreId] = Cast( stor_id as nChar(4) )
     , [StoreName] = Cast( stor_name as nVarchar(50) )
From pubs.dbo.stores