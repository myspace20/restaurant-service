package com.bytebites.restaurant_service.mappers;


import com.bytebites.restaurant_service.dto.MenuResponse;
import com.bytebites.restaurant_service.models.Menu;
import org.mapstruct.Mapper;


import java.util.List;
import java.util.Set;


@Mapper(componentModel = "spring")
public interface MenuMapper {
    MenuResponse toMenuResponse(Menu menu);
    List<MenuResponse> toMenuResponseList(Set<Menu> menus);
}
