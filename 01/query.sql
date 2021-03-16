select u."ID", u."UserName", p."UserName" as "ParentUserName"
from "USER" u
left join "USER" p on p."ID" = u."Parent"
order by u."ID";