/// 新增+ BranchFacilityType
enum BranchFacilityType {
  store('store'),

  v72('v72'),

  v60('v60'),

  rest('rest'),

  $unknown('unknown');

  const BranchFacilityType(this.value);

  factory BranchFacilityType.fromString(String text) => values.firstWhere(
        (e) => e.value == text,
        orElse: () => $unknown,
      );

  final String value;
}

/// 新增+ BranchFacilityIcon
class BranchFacilityIcon {
  /// 设施类型
  late BranchFacilityType type;

  /// 电池数量
  late int number;

  /// 图标缩放比例
  late double scale;

  /// 设施状态 0:不可用, 1:可用
  late int state;

  /// 设施颜色
  String get color {
    if (state == 0) {
      // 设施不可用颜色
      return '#505D6D';
    }
    switch (type) {
      case BranchFacilityType.store:
        return '#00BFFF';
      case BranchFacilityType.v72:
        return '#00DFCA';
      case BranchFacilityType.v60:
        return '#00BFFF';
      case BranchFacilityType.rest:
        return '#FF781D';
      case BranchFacilityType.$unknown:
        return '#FFFFFF';
    }
  }

  BranchFacilityIcon({
    required this.type,
    required this.number,
    required this.scale,
    required this.state,
  });

  /// map => BMFCoordinate
  BranchFacilityIcon.fromMap(Map map)
      : assert(map.containsKey('type'), '必须有设施类型'),
        assert(map.containsKey('number'), '必须有电池数量'),
        assert(map.containsKey('scale'), '必须有缩放比例'),
        assert(map.containsKey('state'), '必须有设施状态') {
    type = BranchFacilityType.fromString(map['type'] as String);
    number = map['number'] as int;
    scale = map['scale'] as double;
    state = map['state'] as int;
  }

  Map<String, Object?> toMap() {
    return {
      'type': type.value,
      'number': number,
      'scale': scale,
      'state': state,
      'color': color,
    };
  }
}
