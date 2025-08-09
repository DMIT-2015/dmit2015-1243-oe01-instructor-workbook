insert into DimPublishers(PublisherId, PublisherName)
Select
    [PublisherId] =  Cast( pub_id as nChar(4) )
     , [PublisherName] = Cast( pub_name as nVarchar(50) )
From pubs.dbo.publishers