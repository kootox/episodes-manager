var seasonController = ['$scope', 'episodes', function ($scope, episodes) {
  $scope.episodes = episodes;

  $scope.createUrl = function() {
    return createUrl();
  }
}];